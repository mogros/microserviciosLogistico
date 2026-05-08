import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { BASE_ENDPOINT } from "../config/app";
import { Cliente } from "../models/cliente";
import { CommonService } from "./common.service";
@Injectable({ providedIn: "root" })
export class ClienteService extends CommonService<Cliente> {
  protected override baseEndPoint = `${BASE_ENDPOINT}/clientes`;
  constructor(http: HttpClient) { super(http); }
  buscar(texto: string): Observable<Cliente[]> { return this.http.get<Cliente[]>(`${this.baseEndPoint}/buscar/${texto}`); }
  porTipo(tipo: string): Observable<Cliente[]> { return this.http.get<Cliente[]>(`${this.baseEndPoint}/tipo/${tipo}`); }
}