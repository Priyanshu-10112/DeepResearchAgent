import axios from 'axios';
import type { ResearchQuery, ResearchResponse, ApiError } from '../types';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 120000, // 2 minutes timeout
});

// Add request interceptor for logging
api.interceptors.request.use(
  (config) => {
    console.log('API Request:', config.method?.toUpperCase(), config.url, config.data);
    return config;
  },
  (error) => {
    console.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

// Add response interceptor for better error handling
api.interceptors.response.use(
  (response) => {
    console.log('API Response:', response.status, response.data);
    return response;
  },
  (error) => {
    if (axios.isAxiosError(error)) {
      if (error.code === 'ECONNABORTED') {
        throw new Error('Request timed out. The server is taking too long to respond. Please try again with a more specific query or fewer sources.');
      }
      const apiError = error.response?.data as ApiError;
      const errorMessage = apiError?.message || error.message || 'An unexpected error occurred';
      console.error('API Error:', {
        status: error.response?.status,
        message: errorMessage,
        data: error.response?.data,
      });
      throw new Error(errorMessage);
    }
    console.error('Unknown Error:', error);
    throw error;
  }
);

export interface ResearchService {
  submitQuery: (query: ResearchQuery) => Promise<ResearchResponse>;
  getReports: () => Promise<ResearchResponse[]>;
  deleteReport: (reportId: string) => Promise<void>;
}

export const researchService: ResearchService = {
  submitQuery: async (query: ResearchQuery) => {
    try {
      // Validate query
      if (!query.query?.trim()) {
        throw new Error('Research query is required');
      }
      if (!query.preferredSources?.length) {
        throw new Error('At least one source must be selected');
      }

      const response = await api.post<ResearchResponse>('/dynamic/research', query);
      return response.data;
    } catch (error) {
      console.error('Error submitting query:', error);
      throw error;
    }
  },

  getReports: async () => {
    try {
      const response = await api.get<ResearchResponse[]>('/reports');
      return response.data || [];
    } catch (error) {
      console.error('Error fetching reports:', error);
      throw error;
    }
  },

  deleteReport: async (reportId: string) => {
    try {
      if (!reportId) {
        throw new Error('Report ID is required');
      }
      await api.delete(`/reports/${reportId}`);
    } catch (error) {
      console.error('Error deleting report:', error);
      throw error;
    }
  },
};

// Available sources for research
export const availableSources: string[] = [
  'serp',
  'openalex',
  'arxiv',
  'reddit',
  'twitter',
  'news', // merged GNEWS into news
]; 