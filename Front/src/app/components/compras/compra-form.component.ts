import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { CompraService } from "../../services/compra.service";
import { ProveedorService } from "../../services/proveedor.service";
import { ProductoService } from "../../services/producto.service";
import { Compra, DetalleCompra } from "../../models/compra";
import { Proveedor } from "../../models/proveedor";
import { Producto } from "../../models/producto";
import Swal from "sweetalert2";

@Component({ selector: "app-compra-form", templateUrl: "./compra-form.component.html" })
export class CompraFormComponent implements OnInit {
  proveedores: Proveedor[] = [];
  productos: Producto[] = [];
  compra: Compra = { proveedorId: 0, proveedorNombre: "", tipoPago: "CONTADO", subtotal: 0, detalles: [] };
  cuotas = 3;
  productoSelId = 0;
  cantidad = 1;

  constructor(
    private svc: CompraService,
    private provSvc: ProveedorService,
    private prodSvc: ProductoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.provSvc.listar().subscribe(d => this.proveedores = d as Proveedor[]);
    this.prodSvc.listar().subscribe(d => this.productos = d as Producto[]);
  }

  /** Al cambiar el select de proveedor, capturar también el nombre */
  onProveedorChange(proveedorId: number) {
    const prov = this.proveedores.find(p => p.id === +proveedorId);
    if (prov) {
      this.compra.proveedorId = prov.id!;
      this.compra.proveedorNombre = prov.razonSocial;
    }
  }

  agregarDetalle() {
    const p = this.productos.find(x => x.id === +this.productoSelId);
    if (!p) return;
    const det: DetalleCompra = {
      productoId: p.id!,
      nombreProducto: p.nombre,
      cantidad: this.cantidad,
      precioUnitario: p.precioCompra,
      subtotal: p.precioCompra * this.cantidad
    };
    this.compra.detalles = [...(this.compra.detalles || []), det];
    this.calcular();
    this.productoSelId = 0;
    this.cantidad = 1;
  }

  quitarDetalle(i: number) {
    this.compra.detalles!.splice(i, 1);
    this.calcular();
  }

  calcular() {
    this.compra.subtotal = (this.compra.detalles || []).reduce((s, d) => s + d.subtotal!, 0);
  }

  get igv() { return +(this.compra.subtotal * 0.18).toFixed(2); }
  get total() { return +(this.compra.subtotal + this.igv).toFixed(2); }

  guardar() {
    if (!this.compra.proveedorId || !this.compra.detalles?.length) {
      Swal.fire("Error", "Selecciona proveedor y al menos un producto", "warning");
      return;
    }
    this.svc.crearCompra(this.compra, this.compra.tipoPago === "CREDITO" ? this.cuotas : 0).subscribe({
      next: c => {
        Swal.fire("Compra creada", `N° ${c.numeroCompra}`, "success");
        this.router.navigate(["/compras"]);
      },
      error: err => Swal.fire("Error", err.error?.message || err.error?.error || "Error al crear compra", "error")
    });
  }
}
