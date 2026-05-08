import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { AuthService } from "../../services/auth.service";
@Component({ selector: "app-navbar", templateUrl: "./navbar.component.html" })
export class NavbarComponent implements OnInit {
  isCollapsed = true; username = ""; isAdmin = false; isVendedor = false; isComprador = false; isLoggedIn = false;
  constructor(private auth: AuthService, private router: Router) {}
  ngOnInit() {
    this.auth.isLoggedIn$.subscribe(ok => {
      this.isLoggedIn = ok;
      if (ok) { this.username = this.auth.getUsername(); this.isAdmin = this.auth.isAdmin(); this.isVendedor = this.auth.isVendedor(); this.isComprador = this.auth.isComprador(); }
    });
  }
  logout() { this.auth.logout(); this.router.navigate(["/login"]); }
}