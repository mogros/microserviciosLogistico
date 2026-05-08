import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { ProductoService, CategoriaService } from "../../services/producto.service";
import { Producto } from "../../models/producto";
import { Categoria } from "../../models/categoria";
import Swal from "sweetalert2";

@Component({ selector: "app-producto-form", templateUrl: "./producto-form.component.html" })
export class ProductoFormComponent implements OnInit {
  titulo = "Nuevo Producto";
  model: Producto = { nombre: "", precioVenta: 0, precioCompra: 0, stock: 0, puntoReorden: 5 };
  categorias: Categoria[] = [];
  error: any = {};

  constructor(
    private svc: ProductoService,
    private catSvc: CategoriaService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    // Cargar categorías primero, luego el producto
    this.catSvc.listar().subscribe(d => {
      this.categorias = d as Categoria[];

      const id = this.route.snapshot.params["id"];
      if (id) {
        this.titulo = "Editar Producto";
        this.svc.ver(+id).subscribe(p => {
          this.model = p;
          // Reasignar la categoría desde la lista para que Angular pueda hacer el match
          if (p.categoria?.id) {
            this.model.categoria = this.categorias.find(c => c.id === p.categoria!.id);
          }
        });
      }
    });
  }

  /**
   * Función de comparación para el select de categoría.
   * Angular compara objetos por referencia por defecto,
   * pero con compareWith podemos comparar por ID.
   */
  compararCategorias(c1: Categoria, c2: Categoria): boolean {
    return c1 && c2 ? c1.id === c2.id : c1 === c2;
  }

  guardar() {
    const obs = this.model.id
      ? this.svc.editar(this.model.id, this.model)
      : this.svc.crear(this.model);

    obs.subscribe({
      next: () => {
        Swal.fire("Guardado", "Producto guardado correctamente", "success");
        this.router.navigate(["/productos"]);
      },
      error: err => { this.error = err.error || {}; }
    });
  }
}
