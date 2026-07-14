import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PushRequest, PushResponse } from './models';

@Injectable({ providedIn: 'root' })
export class GithubAgentService {
  private readonly baseUrl = 'http://localhost:8083';

  constructor(private readonly http: HttpClient) {}

  push(request: PushRequest): Observable<PushResponse> {
    return this.http.post<PushResponse>(`${this.baseUrl}/github/push`, request);
  }
}
