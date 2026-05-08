import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { BASE_ENDPOINT } from "../config/app";
import { Compra, LetraCompra } from "../models/compra";
import { CommonService } from "./common.service";

@Injectable({ providedIn: "root" })
export class CompraService extends CommonService<Compra> {
  protected override baseEndPoint = `${BASE_ENDPOINT}/compras`;
  private cab = new HttpHeaders({ "Content-Type": "application/json" });

  constructor(http: HttpClient) { super(http); }

  crearCompra(compra: Compra, cuotas: number): Observable<Compra> {
    const body = {
      proveedorId:     compra.proveedorId,
      proveedorNombre: compra.proveedorNombre,
      tipoPago:        compra.tipoPago,
      subtotal:        compra.subtotal,
      observaciones:   compra.observaciones,
      cuotas:          cuotas,
      detalles: (compra.detalles || []).map(d => ({
        productoId:     d.productoId,
        nombreProducto: d.nombreProducto,
        cantidad:       d.cantidad,
        precioUnitario: d.precioUnitario
      }))
    };
    return this.http.post<Compra>(`${this.baseEndPoint}/nueva`, body, { headers: this.cab });
  }

  registrarPago(id: number, monto: number): Observable<Compra> {
    return this.http.put<Compra>(`${this.baseEndPoint}/${id}/pago?monto=${monto}`, null);
  }

  pagarLetra(letraId: number): Observable<LetraCompra> {
    return this.http.put<LetraCompra>(`${this.baseEndPoint}/letras/${letraId}/pagar`, null);
  }

  porProveedor(proveedorId: number): Observable<Compra[]> {
    return this.http.get<Compra[]>(`${this.baseEndPoint}/proveedor/${proveedorId}`);
  }

  letras(compraId: number): Observable<LetraCompra[]> {
    return this.http.get<LetraCompra[]>(`${this.baseEndPoint}/${compraId}/letras`);
  }

  letrasVencidas(): Observable<LetraCompra[]> {
    return this.http.get<LetraCompra[]>(`${this.baseEndPoint}/letras/vencidas`);
  }
}
