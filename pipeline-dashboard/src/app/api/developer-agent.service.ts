import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DevelopRequest, DevelopResponse } from './models';

@Injectable({ providedIn: 'root' })
export class DeveloperAgentService {
  private readonly baseUrl = 'http://localhost:8082';

  constructor(private readonly http: HttpClient) {}

  develop(request: DevelopRequest): Observable<DevelopResponse> {
    return this.http.post<DevelopResponse>(`${this.baseUrl}/develop`, request);
  }
}
