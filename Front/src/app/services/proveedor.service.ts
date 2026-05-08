import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { BASE_ENDPOINT } from "../config/app";
import { Proveedor } from "../models/proveedor";
import { Cotizacion } from "../models/cotizacion";
import { CommonService } from "./common.service";
@Injectable({ providedIn: "root" })
export class ProveedorService extends CommonService<Proveedor> {
  protected override baseEndPoint = `${BASE_ENDPOINT}/proveedores`;
  
  constructor(http: HttpClient) { super(http); }

  buscar(texto: string): Observable<Proveedor[]> { 
    return this.http.get<Proveedor[]>(`${this.baseEndPoint}/buscar/${texto}`); 
  }

}
@Injectable({ providedIn: "root" })
export class CotizacionService extends CommonService<Cotizacion> {
  protected override baseEndPoint = `${BASE_ENDPOINT}/cotizaciones`;

  constructor(http: HttpClient) { super(http); }

  aprobar(id: number): Observable<Cotizacion> { 
    return this.http.put<Cotizacion>(`${this.baseEndPoint}/${id}/aprobar`, null); 
  }

  rechazar(id: number): Observable<Cotizacion> { 
    return this.http.put<Cotizacion>(`${this.baseEndPoint}/${id}/rechazar`, null);
   }
   
}