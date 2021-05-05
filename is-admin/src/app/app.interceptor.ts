import { Injectable } from '@angular/core';
import {
  HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpResponse
} from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {CookieService} from "ngx-cookie-service";


/** Pass untouched request through to the next request handler. */
@Injectable()
export class RefreshInterceptor implements HttpInterceptor {

  constructor(private http: HttpClient, private cookieService: CookieService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      tap(
        () => {},
        error => {
          console.log(error);
          if (error.status === 500 && error.error.message === 'refresh fail') {
            this.logout();
          }
        }));
  }

    logout() {
      this.cookieService.delete('demo_access_token', "/", "demo.com")
      this.cookieService.delete('demo_refresh_token', "/", "demo.com")
      this.http.post('logout', {}).subscribe(() => {
        window.location.href = 'http://auth.demo.com:9090/logout?redirect_uri=http://admin.demo.com:8080';
      }, () => {
        alert('logout fail')
      });
    }
}
