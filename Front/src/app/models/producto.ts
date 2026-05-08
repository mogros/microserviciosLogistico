import { Categoria } from "./categoria";
export interface Producto {
  id?: number; nombre: string; descripcion?: string;
  precioVenta: number; precioCompra: number; stock: number;
  puntoReorden: number; unidadMedida?: string; activo?: boolean;
  categoria?: Categoria; bajoStock?: boolean;
}