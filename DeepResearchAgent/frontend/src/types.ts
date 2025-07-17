export interface SourceContent {
    url?: string;
    title: string;
    content: string;
    author?: string;
    publishedDate?: string;
    source: Source;
}

export interface ResearchResult {
    id?: string;
    topic: string;
    summary: string;
    sourceContents: { [key: string]: SourceContent[] };
    keyFindings: string[];
    recommendations: string[];
    confidenceScore: number;
    timestamp: string;
}

export enum Source {
    NEWS = 'NEWS',
    ACADEMIC = 'ACADEMIC',
    FORUM = 'FORUM',
    GNEWS = 'GNEWS'
}

export enum QueryType {
    IMMIGRATION_RULES = 'IMMIGRATION_RULES',
    UNIVERSITY_CHANGES = 'UNIVERSITY_CHANGES',
    COMPETITOR_ACTIVITY = 'COMPETITOR_ACTIVITY',
    GENERAL_RESEARCH = 'GENERAL_RESEARCH'
}

export enum TimeRange {
    LAST_DAY = 'LAST_DAY',
    LAST_WEEK = 'LAST_WEEK',
    LAST_MONTH = 'LAST_MONTH',
    LAST_YEAR = 'LAST_YEAR',
    ALL_TIME = 'ALL_TIME'
}

export interface DynamicQuery {
    type: QueryType;
    timeRange?: TimeRange;
    sources?: Source[];
    keywords?: string[];
    filters?: {
        [key: string]: string;
    };
}

export interface Message {
  id: string;
  content: string;
  timestamp: string;
  role: 'user' | 'assistant';
  results?: ResearchResponse;
}

export interface ApiSource {
  id: string;
  name: string;
  enabled: boolean;
}

export interface ResearchQuery {
    query: string;
    preferredSources: Source[];
    queryType: QueryType;
    timeRange: TimeRange;
    parameters?: Record<string, any>;
}

export interface ResearchResponse {
    topic: string;
    summary: string;
    sourceContents: Record<Source, SourceContent[]>;
    timestamp: string;
    confidenceScore: number;
    keyFindings: string[];
    recommendations: string[];
}

export interface ApiError {
  message: string;
  code?: string;
  details?: any;
} 