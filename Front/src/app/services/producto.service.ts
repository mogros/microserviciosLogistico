import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { BASE_ENDPOINT } from "../config/app";
import { Producto } from "../models/producto";
import { Categoria } from "../models/categoria";
import { CommonService } from "./common.service";
@Injectable({ providedIn: "root" })
export class ProductoService extends CommonService<Producto> {
  protected override baseEndPoint = `${BASE_ENDPOINT}/productos`;
  constructor(http: HttpClient) { super(http); }
  filtrar(nombre: string): Observable<Producto[]> { return this.http.get<Producto[]>(`${this.baseEndPoint}/filtrar/${nombre}`); }
  bajoStock(): Observable<Producto[]> { return this.http.get<Producto[]>(`${this.baseEndPoint}/bajo-stock`); }
  actualizarStock(id: number, cantidad: number): Observable<Producto> { return this.http.put<Producto>(`${this.baseEndPoint}/${id}/stock?cantidad=${cantidad}`, null); }
}
@Injectable({ providedIn: "root" })
export class CategoriaService extends CommonService<Categoria> {
  protected override baseEndPoint = `${BASE_ENDPOINT}/categorias`;
  constructor(http: HttpClient) { super(http); }
}