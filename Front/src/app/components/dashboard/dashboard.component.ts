import { Component, OnInit } from "@angular/core";
import { ReporteService } from "../../services/reporte.service";
import { ProductoService } from "../../services/producto.service";
import { VentaService } from "../../services/venta.service";
import { ChartConfiguration, ChartData, ChartType } from "chart.js";
@Component({ selector: "app-dashboard", templateUrl: "./dashboard.component.html" })
export class DashboardComponent implements OnInit {
  resumen: any = {};
  bajoStock: any[] = []; 
  letrasVencidas: any[] = [];
  barType: ChartType = "bar";
  barData: ChartData<"bar"> = { labels: [], datasets: [] };
  barOptions: ChartConfiguration["options"] = {
    responsive: true, plugins: { legend: { display: true }, title: { display: true, text: "Ventas vs Compras por Mes" } },
    scales: { y: { beginAtZero: true } }
  };
  lineType: ChartType = "line";
  lineData: ChartData<"line"> = { labels: [], datasets: [] };
  lineOptions: ChartConfiguration["options"] = {
    responsive: true, plugins: { legend: { display: false }, title: { display: true, text: "Evolución de Ventas" } },
    scales: { y: { beginAtZero: true } }
  };
  donaType: ChartType = "doughnut";
  donaData: ChartData<"doughnut"> = { labels: [], datasets: [] };
  donaOptions: ChartConfiguration["options"] = {
    responsive: true, plugins: { legend: { position: "bottom" }, title: { display: true, text: "Ventas por Tipo de Pago" } }
  };

  constructor(private reporteService: ReporteService) {

  }

  ngOnInit() { this.cargarTodo(); }
  
  cargarTodo() {
    this.reporteService.resumen().subscribe(r => this.resumen = r);
    this.reporteService.bajoStock().subscribe(d => this.bajoStock = d.slice(0, 5));
    this.reporteService.letrasVencidas().subscribe(d => this.letrasVencidas = d.slice(0, 5));
    this.reporteService.ventasVsCompras().subscribe(d => {
      this.barData = { labels: d.map((x: any) => x.mes), datasets: [
        { label: "Ventas", data: d.map((x: any) => Number(x.ventas)), backgroundColor: "#3b82f6" },
        { label: "Compras", data: d.map((x: any) => Number(x.compras)), backgroundColor: "#f59e0b" }
      ]};
      this.lineData = { labels: d.map((x: any) => x.mes), datasets: [{
        label: "Utilidad", data: d.map((x: any) => Number(x.utilidad)),
        borderColor: "#10b981", backgroundColor: "rgba(16,185,129,0.1)", fill: true, tension: 0.3
      }]};
    });
    this.reporteService.ventasPorTipo().subscribe(d => {
      this.donaData = { labels: ["Contado", "Crédito"], datasets: [{
        data: [Number(d.contado?.monto || 0), Number(d.credito?.monto || 0)],
        backgroundColor: ["#3b82f6", "#f59e0b"]
      }]};
    });
  }
}