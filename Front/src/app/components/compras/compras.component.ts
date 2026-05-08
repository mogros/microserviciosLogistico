import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { CompraService } from "../../services/compra.service";
import { Compra, LetraCompra, DetalleCompra } from "../../models/compra";
import Swal from "sweetalert2";

@Component({ selector: "app-compras", templateUrl: "./compras.component.html" })
export class ComprasComponent implements OnInit {
  lista: Compra[] = [];
  letrasModal: LetraCompra[] = [];
  detallesModal: DetalleCompra[] = [];
  compraSeleccionada?: Compra;
  cargando = false;

  @ViewChild("modalDetalle") modalDetalle!: TemplateRef<any>;
  @ViewChild("modalLetras")  modalLetras!: TemplateRef<any>;

  constructor(private svc: CompraService, private modal: NgbModal) {}

  ngOnInit() { this.cargar(); }

  cargar() {
    this.cargando = true;
    this.svc.listar().subscribe({
      next: d => { this.lista = d as Compra[]; this.cargando = false; },
      error: () => this.cargando = false
    });
  }

  verDetalle(c: Compra) {
    this.compraSeleccionada = c;
    this.detallesModal = c.detalles || [];
    if (!this.detallesModal.length) {
      this.svc.ver(c.id!).subscribe(compra => this.detallesModal = compra.detalles || []);
    }
    this.modal.open(this.modalDetalle, { size: "lg", centered: true });
  }

  verLetras(c: Compra) {
    this.compraSeleccionada = c;
    this.letrasModal = [];
    this.svc.letras(c.id!).subscribe(l => this.letrasModal = l);
    this.modal.open(this.modalLetras, { size: "lg", centered: true });
  }

  pagarLetra(l: LetraCompra) {
    Swal.fire({
      title: "¿Registrar pago?",
      html: `Letra N° <b>${l.numeroLetra}</b> — Monto: <b>S/ ${Number(l.monto).toFixed(2)}</b>`,
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#198754",
      confirmButtonText: "Sí, pagar",
      cancelButtonText: "Cancelar"
    }).then(r => {
      if (r.isConfirmed) {
        this.svc.pagarLetra(l.id!).subscribe(() => {
          l.estado = "PAGADO";
          l.montoPagado = l.monto;
          this.cargar();
          if (this.compraSeleccionada) {
            this.svc.ver(this.compraSeleccionada.id!).subscribe(c => {
              this.compraSeleccionada = c;
            });
          }
          Swal.fire("Pagado", "Letra registrada como pagada", "success");
        });
      }
    });
  }

  anular(c: Compra) {
    Swal.fire({
      title: "¿Anular compra?",
      text: `${c.numeroCompra} — S/ ${Number(c.total).toFixed(2)}`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#dc3545",
      confirmButtonText: "Sí, anular",
      cancelButtonText: "Cancelar"
    }).then(r => {
      if (r.isConfirmed) {
        const compraAnulada = { ...c, estado: "ANULADO" };
        this.svc.editar(c.id!, compraAnulada as any).subscribe(() => {
          c.estado = "ANULADO" as any;
          Swal.fire("Anulada", `Compra ${c.numeroCompra} anulada`, "success");
        });
      }
    });
  }

  badgeEstado(e: string) {
    return e === "PAGADO" ? "badge-pagado"
         : e === "VENCIDO" || e === "ANULADO" ? "badge-vencido"
         : "badge-pendiente";
  }
}
