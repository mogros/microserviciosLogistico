import { Cliente } from "./cliente";
export interface DetalleVenta { id?: number; productoId: number; nombreProducto: string; cantidad: number; precioUnitario: number; subtotal?: number; }
export interface LetraVenta { id?: number; ventaId?: number; numeroLetra?: number; fechaVencimiento: string; monto: number; montoPagado?: number; estado?: "PENDIENTE"|"PAGADO"|"VENCIDO"; fechaPago?: string; }
export interface Venta {
  id?: number; numeroVenta?: string; clienteId: number; clienteNombre?: string;
  fecha?: string; tipoPago: "CONTADO"|"CREDITO"; estado?: "PENDIENTE"|"PARCIAL"|"PAGADO"|"ANULADO";
  subtotal: number; igv?: number; total?: number; montoPagado?: number;
  saldoPendiente?: number; observaciones?: string;
  detalles?: DetalleVenta[]; letras?: LetraVenta[];
}