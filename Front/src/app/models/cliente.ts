export interface Cliente {
  id?: number; nombre: string; apellido?: string; ruc: string;
  email?: string; telefono?: string; direccion?: string;
  tipo?: "REGULAR"|"PREMIUM"|"VIP"; limiteCredito?: number;
  activo?: boolean; fechaRegistro?: string; totalComprasAcumuladas?: number;
}