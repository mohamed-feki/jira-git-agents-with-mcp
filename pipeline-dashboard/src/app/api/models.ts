// ---- jira-agent ----
export interface TicketReadRequest {
  ticketKey: string;
}


// ---- developer-agent ----
export interface DevelopRequest {
  summary: string;
  description: string;
  acceptanceCriteria?: string;
}

export interface GeneratedFile {
  filename: string;
  path: string;
  content: string;
}

export interface TokenUsage {
  promptTokens: number | null;
  completionTokens: number | null;
  totalTokens: number | null;
}

export interface DevelopResponse {
  files: GeneratedFile[];
  tokenUsage: TokenUsage;
}

// ---- github-agent ----
export interface FileEntry {
  path: string;
  content: string;
}

export interface PushRequest {
  repository: string;
  branch: string;
  commitMessage: string;
  files: FileEntry[];
}

export interface PushResponse {
  commitUrl: string;
  commitSha: string;
  repositoryUrl: string;
  filesPushed: number;
  branch: string;
}

// ---- shared error shape ----
export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}
