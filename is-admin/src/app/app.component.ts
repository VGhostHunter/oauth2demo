import { Component } from '@angular/core';
import { HttpClient } from "@angular/common/http";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'microservice security';
  authenticated = false;
  credentials = {username: 'xixi', password: '123456'}
  order = {}

  constructor(private http: HttpClient) {
    this.http.get("me").subscribe(data => {
      if(data) {
        this.authenticated = true;
      }

      if(!this.authenticated) {
        window.location.href = "http://auth.demo.com:9090/oauth/authorize?" +
          "client_id=admin&" +
          "redirect_uri=http://admin.demo.com:8080/oauth/callback&" +
          "response_type=code&" +
          "state=abc";
      }
    })
  }

  login() {
    this.http.post('login', this.credentials).subscribe(() => {
      this.authenticated = true;
    }, () => {
      alert('login fail')
    });
  }

  getOrder() {
    this.http.get('api/order/orders/1').subscribe(data => {
      this.order = data;
    }, () => {
      alert('get order fail')
    })
  }

  logout() {
    this.http.post('logout', null).subscribe(() => {
      window.location.href = "http://auth.demo.com:9090/logout?redirect_uri=http://admin.demo.com:8080"
    }, () => {
      alert('logout fail')
    })
  }
}
