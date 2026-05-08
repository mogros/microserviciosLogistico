import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { VentaService } from "../../services/venta.service";
import { ClienteService } from "../../services/cliente.service";
import { ProductoService } from "../../services/producto.service";
import { Venta, DetalleVenta } from "../../models/venta";
import { Cliente } from "../../models/cliente";
import { Producto } from "../../models/producto";
import Swal from "sweetalert2";

@Component({ selector: "app-venta-form", templateUrl: "./venta-form.component.html" })
export class VentaFormComponent implements OnInit {
  clientes: Cliente[] = [];
  productos: Producto[] = [];
  venta: Venta = { clienteId: 0, clienteNombre: "", tipoPago: "CONTADO", subtotal: 0, detalles: [] };
  cuotas = 3;
  productoSelId = 0;
  cantidad = 1;

  constructor(
    private svc: VentaService,
    private clienteSvc: ClienteService,
    private productoSvc: ProductoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.clienteSvc.listar().subscribe(d => this.clientes = d as Cliente[]);
    this.productoSvc.listar().subscribe(d => this.productos = d as Producto[]);
  }

  onClienteChange(clienteId: number) {
    const cliente = this.clientes.find(c => c.id === +clienteId);
    if (cliente) {
      this.venta.clienteId = cliente.id!;
      this.venta.clienteNombre = `${cliente.nombre} ${cliente.apellido || ""}`.trim();
    }
  }

  get productoSeleccionado(): Producto | undefined {
    return this.productos.find(p => p.id === +this.productoSelId);
  }

  agregarDetalle() {
    const p = this.productos.find(x => x.id === +this.productoSelId);
    if (!p) return;

    // Validar stock en el frontend también
    const stockDisponible = p.stock || 0;
    if (this.cantidad > stockDisponible) {
      Swal.fire("Stock insuficiente",
        `Solo hay <b>${stockDisponible}</b> unidades de <b>${p.nombre}</b> disponibles.`,
        "warning");
      return;
    }

    const det: DetalleVenta = {
      productoId: p.id!,
      nombreProducto: p.nombre,
      cantidad: this.cantidad,
      precioUnitario: p.precioVenta,
      subtotal: p.precioVenta * this.cantidad
    };
    this.venta.detalles = [...(this.venta.detalles || []), det];
    this.calcularSubtotal();
    this.productoSelId = 0;
    this.cantidad = 1;
  }

  quitarDetalle(i: number) {
    this.venta.detalles!.splice(i, 1);
    this.calcularSubtotal();
  }

  calcularSubtotal() {
    this.venta.subtotal = (this.venta.detalles || []).reduce((s, d) => s + d.subtotal!, 0);
  }

  get igvEstimado() { return +(this.venta.subtotal * 0.18).toFixed(2); }
  get totalEstimado() { return +(this.venta.subtotal + this.igvEstimado).toFixed(2); }

  guardar() {
    if (!this.venta.clienteId || !this.venta.detalles?.length) {
      Swal.fire("Error", "Selecciona cliente y al menos un producto", "warning");
      return;
    }
    this.svc.crearVenta(this.venta, this.venta.tipoPago === "CREDITO" ? this.cuotas : 0).subscribe({
      next: v => {
        Swal.fire("Venta creada", `N° ${v.numeroVenta}`, "success");
        this.router.navigate(["/ventas"]);
      },
      error: err => {
        // Mostrar el mensaje específico del backend (ej: "Stock insuficiente para...")
        const msg = err.error?.error || err.error?.message || err.message || "Error al crear venta";
        Swal.fire("Error", msg, "error");
      }
    });
  }
}
