import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router, ActivatedRoute } from "@angular/router";
import { AuthService } from "../../services/auth.service";
@Component({ selector: "app-login", templateUrl: "./login.component.html" })
export class LoginComponent implements OnInit {
  form!: FormGroup; 
  loading = false;
   error = ""; 
   returnUrl = "/dashboard";
  
   constructor(private fb: FormBuilder, private auth: AuthService, 
    private router: Router, private route: ActivatedRoute) {

    }

  ngOnInit() {
    if (this.auth.hasToken()) { 
      this.router.navigate([this.returnUrl]); 
      return; 
    }
    this.returnUrl = this.route.snapshot.queryParams["returnUrl"] || "/dashboard";
    this.form = this.fb.group({ username: ["", [Validators.required]], password: ["", [Validators.required]] });
  }
  get username() { return this.form.get("username")!; }

  get password() { return this.form.get("password")!; }
  
  onSubmit() {
    if (this.form.invalid) return;
    this.loading = true; this.error = "";
    this.auth.login(this.form.value).subscribe({
      next: () => this.router.navigate([this.returnUrl]),
      error: err => { this.loading = false; this.error = err.status === 401 ? "Credenciales incorrectas" : `Error ${err.status}: ${err.error?.message || "Error de conexión"}`; }
    });
  }
}