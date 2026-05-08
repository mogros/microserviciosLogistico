import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { ClienteService } from "../../services/cliente.service";
import { Cliente } from "../../models/cliente";
import Swal from "sweetalert2";
@Component({ selector: "app-cliente-form", templateUrl: "./cliente-form.component.html" })
export class ClienteFormComponent implements OnInit {
  titulo = "Nuevo Cliente"; model: Cliente = { nombre:"", ruc:"" }; error: any = {};
  constructor(private svc: ClienteService, private route: ActivatedRoute, private router: Router) {}
  ngOnInit() { const id = this.route.snapshot.params["id"]; if (id) { this.titulo = "Editar Cliente"; this.svc.ver(+id).subscribe(c => this.model = c); } }
  guardar() {
    const obs = this.model.id ? this.svc.editar(this.model.id, this.model) : this.svc.crear(this.model);
    obs.subscribe({ next: () => { Swal.fire("Guardado","","success"); this.router.navigate(["/clientes"]); }, error: err => this.error = err.error || {} });
  }
}