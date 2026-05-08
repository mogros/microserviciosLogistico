import { Component, OnInit } from "@angular/core";
import { ProductoService } from "../../services/producto.service";
import { Producto } from "../../models/producto";
import Swal from "sweetalert2";
@Component({ selector: "app-productos", templateUrl: "./productos.component.html" })
export class ProductosComponent implements OnInit {
  lista: Producto[] = []; 
  cargando = false;

  constructor(private svc: ProductoService) {

  }

  ngOnInit() { this.cargar(); }

  cargar() { 
    this.cargando = true; 
    this.svc.listar().subscribe({ next: d => { this.lista = d as Producto[]; 
      this.cargando = false; }, error: () => this.cargando = false }); 
    }

  eliminar(p: Producto) {
    Swal.fire({ title:"¿Eliminar?", text:`Producto: ${p.nombre}`, icon:"warning", showCancelButton:true, confirmButtonColor:"#ef4444", confirmButtonText:"Sí, eliminar" })
    .then(r => { if (r.isConfirmed) this.svc.eliminar(p.id!).subscribe(() => { this.lista = this.lista.filter(x => x.id !== p.id); 
                                                        Swal.fire("Eliminado","","success");
        }); 
      });
  }
}