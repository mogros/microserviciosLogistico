import { Injectable } from "@angular/core";
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";
@Injectable({ providedIn: "root" })
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}
  canActivate(_: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.auth.hasToken()) return true;
    this.router.navigate(["/login"], { queryParams: { returnUrl: state.url } }); return false;
  }
}
@Injectable({ providedIn: "root" })
export class RolGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}
  canActivate(route: ActivatedRouteSnapshot): boolean {
    const roles: string[] = route.data?.["roles"] ?? [];
    if (!roles.length || roles.some(r => this.auth.hasRole(r))) return true;
    this.router.navigate(["/dashboard"]); return false;
  }
}