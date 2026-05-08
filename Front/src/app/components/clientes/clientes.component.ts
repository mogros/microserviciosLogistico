import { Component, OnInit } from "@angular/core";
import { ClienteService } from "../../services/cliente.service";
import { Cliente } from "../../models/cliente";
import Swal from "sweetalert2";
@Component({ selector: "app-clientes", templateUrl: "./clientes.component.html" })
export class ClientesComponent implements OnInit {
  lista: Cliente[] = []; cargando = false;
  constructor(private svc: ClienteService) {}
  ngOnInit() { this.cargar(); }
  cargar() { this.cargando = true; this.svc.listar().subscribe({ next: d => { this.lista = d as Cliente[]; this.cargando = false; }, error: () => this.cargando = false }); }
  badgeClase(tipo: string) { return tipo === "VIP" ? "badge-vip" : tipo === "PREMIUM" ? "badge-premium" : "badge-pendiente"; }
  eliminar(c: Cliente) {
    Swal.fire({ title:"¿Eliminar?", text:c.nombre, icon:"warning", showCancelButton:true, confirmButtonColor:"#ef4444", confirmButtonText:"Sí, eliminar" })
    .then(r => { if (r.isConfirmed) this.svc.eliminar(c.id!).subscribe(() => { this.lista = this.lista.filter(x => x.id !== c.id); Swal.fire("Eliminado","","success"); }); });
  }
}