import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable()
export abstract class CommonService<E> {
  protected baseEndPoint!: string;
  protected cabeceras = new HttpHeaders({ "Content-Type": "application/json" });
  constructor(protected http: HttpClient) {}
  listar(): Observable<E[]> { return this.http.get<E[]>(this.baseEndPoint); }
  listarPaginas(page: string, size: string): Observable<any> {
    const params = new HttpParams().set("page", page).set("size", size);
    return this.http.get<any>(`${this.baseEndPoint}/pagina`, { params });
  }
  ver(id: number): Observable<E> { return this.http.get<E>(`${this.baseEndPoint}/${id}`); }
  crear(e: E): Observable<E> { return this.http.post<E>(this.baseEndPoint, e, { headers: this.cabeceras }); }
  editar(id: number, e: E): Observable<E> { return this.http.put<E>(`${this.baseEndPoint}/${id}`, e, { headers: this.cabeceras }); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.baseEndPoint}/${id}`); }
}