import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { BASE_ENDPOINT } from "../config/app";
import { Venta, LetraVenta } from "../models/venta";
import { CommonService } from "./common.service";

@Injectable({ providedIn: "root" })
export class VentaService extends CommonService<Venta> {
  protected override baseEndPoint = `${BASE_ENDPOINT}/ventas`;
  private cab = new HttpHeaders({ "Content-Type": "application/json" });

  constructor(http: HttpClient) { super(http); }

  /**
   * Envia el DTO plano directamente al endpoint /nueva.
   * El backend espera un VentaRequest (no {venta: ..., cuotas: ...}).
   */
  crearVenta(venta: Venta, cuotas: number): Observable<Venta> {
    const body = {
      clienteId:      venta.clienteId,
      clienteNombre:  venta.clienteNombre,
      tipoPago:       venta.tipoPago,
      subtotal:       venta.subtotal,
      observaciones:  venta.observaciones,
      cuotas:         cuotas,
      detalles: (venta.detalles || []).map(d => ({
        productoId:     d.productoId,
        nombreProducto: d.nombreProducto,
        cantidad:       d.cantidad,
        precioUnitario: d.precioUnitario
      }))
    };
    return this.http.post<Venta>(`${this.baseEndPoint}/nueva`, body, { headers: this.cab });
  }

  registrarPago(id: number, monto: number): Observable<Venta> {
    return this.http.put<Venta>(`${this.baseEndPoint}/${id}/pago?monto=${monto}`, null);
  }

  pagarLetra(letraId: number): Observable<LetraVenta> {
    return this.http.put<LetraVenta>(`${this.baseEndPoint}/letras/${letraId}/pagar`, null);
  }

  anularVenta(id: number): Observable<Venta> {
    return this.http.put<Venta>(`${this.baseEndPoint}/${id}/anular`, null);
  }

  porCliente(clienteId: number): Observable<Venta[]> {
    return this.http.get<Venta[]>(`${this.baseEndPoint}/cliente/${clienteId}`);
  }

  letras(ventaId: number): Observable<LetraVenta[]> {
    return this.http.get<LetraVenta[]>(`${this.baseEndPoint}/${ventaId}/letras`);
  }

  letrasVencidas(): Observable<LetraVenta[]> {
    return this.http.get<LetraVenta[]>(`${this.baseEndPoint}/letras/vencidas`);
  }

  porPeriodo(inicio: string, fin: string): Observable<Venta[]> {
    return this.http.get<Venta[]>(`${this.baseEndPoint}/periodo?inicio=${inicio}&fin=${fin}`);
  }
}
