import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { VentaService } from "../../services/venta.service";
import { Venta, LetraVenta, DetalleVenta } from "../../models/venta";
import Swal from "sweetalert2";

@Component({ selector: "app-ventas", templateUrl: "./ventas.component.html" })
export class VentasComponent implements OnInit {
  lista: Venta[] = [];
  letrasModal: LetraVenta[] = [];
  detallesModal: DetalleVenta[] = [];
  ventaSeleccionada?: Venta;
  cargando = false;

  @ViewChild("modalDetalle") modalDetalle!: TemplateRef<any>;
  @ViewChild("modalLetras")  modalLetras!: TemplateRef<any>;

  constructor(private svc: VentaService, private modal: NgbModal) {}

  ngOnInit() { this.cargar(); }

  cargar() {
    this.cargando = true;
    this.svc.listar().subscribe({
      next: d => { this.lista = d as Venta[]; this.cargando = false; },
      error: () => this.cargando = false
    });
  }

  verDetalle(v: Venta) {
    this.ventaSeleccionada = v;
    this.detallesModal = v.detalles || [];
    if (!this.detallesModal.length) {
      this.svc.ver(v.id!).subscribe(venta => this.detallesModal = venta.detalles || []);
    }
    this.modal.open(this.modalDetalle, { size: "lg", centered: true });
  }

  verLetras(v: Venta) {
    this.ventaSeleccionada = v;
    this.letrasModal = [];
    this.svc.letras(v.id!).subscribe(l => this.letrasModal = l);
    this.modal.open(this.modalLetras, { size: "lg", centered: true });
  }

  pagarLetra(l: LetraVenta) {
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
          if (this.ventaSeleccionada) {
            this.svc.ver(this.ventaSeleccionada.id!).subscribe(v => {
              this.ventaSeleccionada = v;
              const idx = this.lista.findIndex(x => x.id === v.id);
              if (idx >= 0) this.lista[idx] = v;
            });
          }
          Swal.fire("Pagado", "Letra registrada como pagada", "success");
        });
      }
    });
  }

  anular(v: Venta) {
    Swal.fire({
      title: "¿Anular venta?",
      text: `${v.numeroVenta} — S/ ${Number(v.total).toFixed(2)}`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#dc3545",
      confirmButtonText: "Sí, anular",
      cancelButtonText: "Cancelar"
    }).then(r => {
      if (r.isConfirmed) {
        const ventaAnulada = { ...v, estado: "ANULADO" };
        this.svc.editar(v.id!, ventaAnulada as any).subscribe(() => {
          v.estado = "ANULADO" as any;
          Swal.fire("Anulada", `Venta ${v.numeroVenta} anulada`, "success");
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
