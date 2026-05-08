import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable, BehaviorSubject, tap } from "rxjs";
import { BASE_ENDPOINT } from "../config/app";

export interface LoginRequest { username: string; password: string; }
export interface JwtResponse { token: string; type: string; id: number; username: string; email: string; roles: string[]; }

@Injectable({ providedIn: "root" })
export class AuthService {
  private readonly AUTH = `${BASE_ENDPOINT}/auth`;
  private readonly TOKEN_KEY = "log_token";
  private readonly USER_KEY = "log_user";
  private loggedIn$ = new BehaviorSubject<boolean>(this.hasToken());
  public isLoggedIn$ = this.loggedIn$.asObservable();

  constructor(private http: HttpClient) {}

  login(req: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.AUTH}/login`, req, { headers: new HttpHeaders({ "Content-Type": "application/json" }) })
      .pipe(tap(r => { sessionStorage.setItem(this.TOKEN_KEY, r.token); const {token,...u} = r; sessionStorage.setItem(this.USER_KEY, JSON.stringify(u)); this.loggedIn$.next(true); }));
  }

  logout() { sessionStorage.clear(); this.loggedIn$.next(false); }

  getToken(): string | null { return sessionStorage.getItem(this.TOKEN_KEY); }

  hasToken(): boolean {
    const t = this.getToken(); if (!t) return false;
    try { const p = JSON.parse(atob(t.split(".")[1])); return Date.now() < p.exp * 1000; } catch { return false; }
  }
  getUser(): any { const u = sessionStorage.getItem(this.USER_KEY); return u ? JSON.parse(u) : null; }
  
  getUsername(): string { return this.getUser()?.username ?? ""; }

  getRoles(): string[] { return this.getUser()?.roles ?? []; }

  hasRole(r: string): boolean { return this.getRoles().includes(r); }

  isAdmin(): boolean { return this.hasRole("ROLE_ADMIN"); }

  isVendedor(): boolean { return this.hasRole("ROLE_VENDEDOR") || this.isAdmin(); }

  isComprador(): boolean { return this.hasRole("ROLE_COMPRADOR") || this.isAdmin(); }
  
}