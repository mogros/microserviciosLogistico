import { Proveedor } from "./proveedor";
export interface DetalleCompra { id?: number; productoId: number; nombreProducto: string; cantidad: number; precioUnitario: number; subtotal?: number; }
export interface LetraCompra { id?: number; compraId?: number; numeroLetra?: number; fechaVencimiento: string; monto: number; montoPagado?: number; estado?: "PENDIENTE"|"PAGADO"|"VENCIDO"; fechaPago?: string; }
export interface Compra {
  id?: number; numeroCompra?: string; proveedorId: number; proveedorNombre?: string;
  fecha?: string; tipoPago: "CONTADO"|"CREDITO"; estado?: "PENDIENTE"|"PARCIAL"|"PAGADO"|"ANULADO";
  subtotal: number; igv?: number; total?: number; montoPagado?: number;
  saldoPendiente?: number; observaciones?: string;
  detalles?: DetalleCompra[]; letras?: LetraCompra[];
}