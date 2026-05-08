import { Proveedor } from "./proveedor";
export interface DetalleCotizacion { id?: number; productoId: number; nombreProducto: string; cantidad: number; precioUnitario: number; subtotal?: number; }
export interface Cotizacion {
  id?: number; proveedor?: Proveedor; fecha?: string; fechaVencimiento?: string;
  estado?: "PENDIENTE"|"APROBADA"|"RECHAZADA"|"VENCIDA";
  observaciones?: string; total?: number; detalles?: DetalleCotizacion[];
}