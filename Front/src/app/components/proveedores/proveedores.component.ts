import { Component, OnInit } from "@angular/core";
import { ProveedorService } from "../../services/proveedor.service";
import { Proveedor } from "../../models/proveedor";
import Swal from "sweetalert2";
@Component({ selector: "app-proveedores", templateUrl: "./proveedores.component.html" })
export class ProveedoresComponent implements OnInit {
  lista: Proveedor[] = []; cargando = false;
  constructor(private svc: ProveedorService) {}
  ngOnInit() { this.cargar(); }
  cargar() { this.cargando = true; this.svc.listar().subscribe({ next: d => { this.lista = d as Proveedor[]; this.cargando = false; }, error: () => this.cargando = false }); }
  eliminar(p: Proveedor) {
    Swal.fire({ title:"¿Eliminar?", text: p.razonSocial, icon:"warning", showCancelButton:true, confirmButtonColor:"#ef4444", confirmButtonText:"Sí" })
    .then(r => { if (r.isConfirmed) this.svc.eliminar(p.id!).subscribe(() => { this.lista = this.lista.filter(x => x.id !== p.id); Swal.fire("Eliminado","","success"); }); });
  }
}