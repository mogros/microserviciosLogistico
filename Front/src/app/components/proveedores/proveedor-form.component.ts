import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { ProveedorService } from "../../services/proveedor.service";
import { Proveedor } from "../../models/proveedor";
import Swal from "sweetalert2";
@Component({ selector: "app-proveedor-form", templateUrl: "./proveedor-form.component.html" })
export class ProveedorFormComponent implements OnInit {
  titulo = "Nuevo Proveedor"; model: Proveedor = { razonSocial:"", ruc:"" }; error: any = {};
  constructor(private svc: ProveedorService, private route: ActivatedRoute, private router: Router) {}
  ngOnInit() { const id = this.route.snapshot.params["id"]; if (id) { this.titulo = "Editar Proveedor"; this.svc.ver(+id).subscribe(p => this.model = p); } }
  guardar() {
    const obs = this.model.id ? this.svc.editar(this.model.id, this.model) : this.svc.crear(this.model);
    obs.subscribe({ next: () => { Swal.fire("Guardado","","success"); this.router.navigate(["/proveedores"]); }, error: err => this.error = err.error || {} });
  }
}