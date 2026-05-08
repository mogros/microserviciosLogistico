import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { BASE_ENDPOINT } from "../config/app";
@Injectable({ providedIn: "root" })
export class ReporteService {
  private base = `${BASE_ENDPOINT}/reportes`;

  constructor(private http: HttpClient) {}

  resumen(): Observable<any> { 
    return this.http.get<any>(`${this.base}/resumen`); 
  }

  ventasPorMes(): Observable<any[]> {
     return this.http.get<any[]>(`${this.base}/ventas-por-mes`); 
    }

  ventasVsCompras(): Observable<any[]> { 
    return this.http.get<any[]>(`${this.base}/ventas-vs-compras`);
   }

  topClientes(): Observable<any[]> { 
    return this.http.get<any[]>(`${this.base}/top-clientes`); 
  }

  ventasPorTipo(): Observable<any> { 
    return this.http.get<any>(`${this.base}/ventas-por-tipo`);
   }

  bajoStock(): Observable<any[]> {
     return this.http.get<any[]>(`${this.base}/bajo-stock`); 
    }

  letrasVencidas(): Observable<any[]> { 
    return this.http.get<any[]>(`${this.base}/letras-vencidas`); 
  }
  
}