import { Component, OnInit } from "@angular/core";
import { ReporteService } from "../../services/reporte.service";
import { ChartConfiguration, ChartData, ChartType } from "chart.js";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
@Component({ selector: "app-reportes", templateUrl: "./reportes.component.html", styleUrls: ["./reportes.component.css"] })
export class ReportesComponent implements OnInit {
  tabActiva = 0; cargando = false; resumen: any = {};
  ventasMes: any[] = []; ventasVsCompras: any[] = []; topClientes: any[] = [];
  bajoStock: any[] = []; letrasVencidas: any[] = []; ventasTipo: any = {};

  barType: ChartType = "bar";
  barData: ChartData<"bar"> = { labels:[], datasets:[] };
  barOptions: ChartConfiguration["options"] = { responsive:true, plugins:{ legend:{display:true}, title:{display:true,text:"Ventas vs Compras por Mes"} }, scales:{y:{beginAtZero:true}} };

  lineType: ChartType = "line";
  lineData: ChartData<"line"> = { labels:[], datasets:[] };
  lineOptions: ChartConfiguration["options"] = { responsive:true, plugins:{ legend:{display:false}, title:{display:true,text:"Tendencia de Ventas"} }, scales:{y:{beginAtZero:true}} };

  donaType: ChartType = "doughnut";
  donaData: ChartData<"doughnut"> = { labels:[], datasets:[] };
  donaOptions: ChartConfiguration["options"] = { responsive:true, plugins:{ legend:{position:"bottom"}, title:{display:true,text:"Tipo de Pago"} } };

  hbarType: ChartType = "bar";
  hbarData: ChartData<"bar"> = { labels:[], datasets:[] };
  hbarOptions: ChartConfiguration["options"] = { indexAxis:"y", responsive:true, plugins:{ legend:{display:false}, title:{display:true,text:"Top 10 Clientes"} }, scales:{x:{beginAtZero:true}} };

  constructor(private svc: ReporteService) {}
  ngOnInit() { this.cargar(); }

  cargar() {
    this.cargando = true;
    this.svc.resumen().subscribe(r => this.resumen = r);
    this.svc.bajoStock().subscribe(d => this.bajoStock = d);
    this.svc.letrasVencidas().subscribe(d => this.letrasVencidas = d);
    this.svc.ventasPorMes().subscribe(d => {
      this.ventasMes = d;
      this.lineData = { labels: d.map((x:any) => x.mes), datasets: [{ label:"Ventas", data: d.map((x:any) => Number(x.total)), borderColor:"#3b82f6", backgroundColor:"rgba(59,130,246,0.1)", fill:true, tension:0.3 }] };
    });
    this.svc.ventasVsCompras().subscribe(d => {
      this.ventasVsCompras = d;
      this.barData = { labels: d.map((x:any) => x.mes), datasets: [
        { label:"Ventas", data:d.map((x:any) => Number(x.ventas)), backgroundColor:"#3b82f6" },
        { label:"Compras", data:d.map((x:any) => Number(x.compras)), backgroundColor:"#f59e0b" }
      ]};
      this.cargando = false;
    });
    this.svc.topClientes().subscribe(d => {
      this.topClientes = d;
      this.hbarData = { labels:d.map((x:any) => x.nombre), datasets:[{ data:d.map((x:any) => Number(x.totalAcumulado)), backgroundColor:"#8b5cf6" }] };
    });
    this.svc.ventasPorTipo().subscribe(d => {
      this.ventasTipo = d;
      this.donaData = { labels:["Contado","Crédito"], datasets:[{ data:[Number(d.contado?.cantidad||0),Number(d.credito?.cantidad||0)], backgroundColor:["#3b82f6","#f59e0b"] }] };
    });
  }

  exportarResumen() { this.exportar([{ "Total Ventas":this.resumen.totalVentas,"Total Compras":this.resumen.totalCompras,"Utilidad Bruta":this.resumen.utilidadBruta,"N° Ventas":this.resumen.cantidadVentas,"N° Compras":this.resumen.cantidadCompras,"Clientes":this.resumen.totalClientes,"Premium/VIP":this.resumen.clientesPremium,"Bajo Stock":this.resumen.productosBajoStock,"Letras Vencidas":this.resumen.letrasVencidas }], "Resumen"); }
  exportarVentasMes() { this.exportar(this.ventasMes.map(d => ({ "Mes":d.mes,"Total Ventas":d.total })), "Ventas_por_Mes"); }
  exportarVentasVsCompras() { this.exportar(this.ventasVsCompras.map(d => ({ "Mes":d.mes,"Ventas":d.ventas,"Compras":d.compras,"Utilidad":d.utilidad })), "Ventas_vs_Compras"); }
  exportarTopClientes() { this.exportar(this.topClientes.map(d => ({ "Cliente":d.nombre,"Tipo":d.tipo,"Total Acumulado":d.totalAcumulado })), "Top_Clientes"); }
  exportarBajoStock() { this.exportar(this.bajoStock.map(d => ({ "Producto":d.nombre,"Stock":d.stock,"Punto Reorden":d.puntoReorden,"Categoría":d.categoria?.nombre||"-" })), "Bajo_Stock"); }
  exportarLetrasVencidas() { this.exportar(this.letrasVencidas.map(d => ({ "N°":d.numeroLetra,"Vencimiento":d.fechaVencimiento,"Monto":d.monto,"Estado":d.estado })), "Letras_Vencidas"); }

  private exportar(rows: any[], nombre: string) {
    if (!rows.length) return;
    const ws = XLSX.utils.json_to_sheet(rows);
    const cols = Object.keys(rows[0]).map(k => ({ wch: Math.max(k.length, ...rows.map(r => String(r[k]).length)) + 2 }));
    ws["!cols"] = cols;
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, nombre.substring(0,31));
    saveAs(new Blob([XLSX.write(wb, { bookType:"xlsx", type:"array" })], { type:"application/octet-stream" }), `${nombre}_${new Date().toISOString().slice(0,10)}.xlsx`);
  }
}