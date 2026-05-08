import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { LoginComponent } from "./components/auth/login.component";
import { DashboardComponent } from "./components/dashboard/dashboard.component";
import { ProductosComponent } from "./components/productos/productos.component";
import { ProductoFormComponent } from "./components/productos/producto-form.component";
import { ClientesComponent } from "./components/clientes/clientes.component";
import { ClienteFormComponent } from "./components/clientes/cliente-form.component";
import { ProveedoresComponent } from "./components/proveedores/proveedores.component";
import { ProveedorFormComponent } from "./components/proveedores/proveedor-form.component";
import { VentasComponent } from "./components/ventas/ventas.component";
import { VentaFormComponent } from "./components/ventas/venta-form.component";
import { ComprasComponent } from "./components/compras/compras.component";
import { CompraFormComponent } from "./components/compras/compra-form.component";
import { ReportesComponent } from "./components/reportes/reportes.component";
import { AuthGuard, RolGuard } from "./guards/auth.guard";

const routes: Routes = [
  { path: "login", component: LoginComponent },
  { path: "", pathMatch: "full", redirectTo: "dashboard" },
  { path: "dashboard",       component: DashboardComponent,    canActivate: [AuthGuard] },
  { path: "productos",       component: ProductosComponent,    canActivate: [AuthGuard] },
  { path: "productos/form",  component: ProductoFormComponent, canActivate: [AuthGuard, RolGuard], data: { roles: ["ROLE_ADMIN","ROLE_COMPRADOR"] } },
  { path: "productos/form/:id", component: ProductoFormComponent, canActivate: [AuthGuard, RolGuard], data: { roles: ["ROLE_ADMIN","ROLE_COMPRADOR"] } },
  { path: "clientes",        component: ClientesComponent,     canActivate: [AuthGuard] },
  { path: "clientes/form",   component: ClienteFormComponent,  canActivate: [AuthGuard, RolGuard], data: { roles: ["ROLE_ADMIN","ROLE_VENDEDOR"] } },
  { path: "clientes/form/:id", component: ClienteFormComponent, canActivate: [AuthGuard, RolGuard], data: { roles: ["ROLE_ADMIN","ROLE_VENDEDOR"] } },
  { path: "proveedores",     component: ProveedoresComponent,  canActivate: [AuthGuard] },
  { path: "proveedores/form", component: ProveedorFormComponent, canActivate: [AuthGuard, RolGuard], data: { roles: ["ROLE_ADMIN","ROLE_COMPRADOR"] } },
  { path: "proveedores/form/:id", component: ProveedorFormComponent, canActivate: [AuthGuard, RolGuard], data: { roles: ["ROLE_ADMIN","ROLE_COMPRADOR"] } },
  { path: "ventas",          component: VentasComponent,       canActivate: [AuthGuard, RolGuard], data: { roles: ["ROLE_ADMIN","ROLE_VENDEDOR"] } },
  { path: "ventas/form",     component: VentaFormComponent,    canActivate: [AuthGuard, RolGuard], data: { roles: ["ROLE_ADMIN","ROLE_VENDEDOR"] } },
  { path: "compras",         component: ComprasComponent,      canActivate: [AuthGuard, RolGuard], data: { roles: ["ROLE_ADMIN","ROLE_COMPRADOR"] } },
  { path: "compras/form",    component: CompraFormComponent,   canActivate: [AuthGuard, RolGuard], data: { roles: ["ROLE_ADMIN","ROLE_COMPRADOR"] } },
  { path: "reportes",        component: ReportesComponent,     canActivate: [AuthGuard, RolGuard], data: { roles: ["ROLE_ADMIN"] } },
  { path: "**", redirectTo: "dashboard" }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}