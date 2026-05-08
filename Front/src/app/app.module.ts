import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { HTTP_INTERCEPTORS, HttpClientModule } from "@angular/common/http";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { RouterModule } from "@angular/router";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { NgChartsModule } from "ng2-charts";
// Angular Material
import { MatTabsModule } from "@angular/material/tabs";
import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatButtonModule } from "@angular/material/button";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { MatCardModule } from "@angular/material/card";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { DatePipe, DecimalPipe } from "@angular/common";

import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { JwtInterceptor } from "./interceptors/jwt.interceptor";

// Layout
import { NavbarComponent } from "./layout/navbar/navbar.component";

// Auth
import { LoginComponent } from "./components/auth/login.component";

// Dashboard
import { DashboardComponent } from "./components/dashboard/dashboard.component";

// Productos
import { ProductosComponent } from "./components/productos/productos.component";
import { ProductoFormComponent } from "./components/productos/producto-form.component";

// Clientes
import { ClientesComponent } from "./components/clientes/clientes.component";
import { ClienteFormComponent } from "./components/clientes/cliente-form.component";

// Proveedores
import { ProveedoresComponent } from "./components/proveedores/proveedores.component";
import { ProveedorFormComponent } from "./components/proveedores/proveedor-form.component";

// Ventas
import { VentasComponent } from "./components/ventas/ventas.component";
import { VentaFormComponent } from "./components/ventas/venta-form.component";

// Compras
import { ComprasComponent } from "./components/compras/compras.component";
import { CompraFormComponent } from "./components/compras/compra-form.component";

// Reportes
import { ReportesComponent } from "./components/reportes/reportes.component";

@NgModule({
  declarations: [
    AppComponent, NavbarComponent, LoginComponent, DashboardComponent,
    ProductosComponent, ProductoFormComponent,
    ClientesComponent, ClienteFormComponent,
    ProveedoresComponent, ProveedorFormComponent,
    VentasComponent, VentaFormComponent,
    ComprasComponent, CompraFormComponent,
    ReportesComponent
  ],
  imports: [
    BrowserModule, BrowserAnimationsModule, HttpClientModule,
    FormsModule, ReactiveFormsModule, NgbModule, NgChartsModule,
    MatTabsModule, MatTableModule, MatPaginatorModule, MatButtonModule,
    MatInputModule, MatSelectModule, MatCardModule, MatProgressSpinnerModule,
    AppRoutingModule
  ],
  providers: [
    DatePipe, DecimalPipe,
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}