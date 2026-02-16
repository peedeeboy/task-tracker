import { app } from '../../../main/app';

import request from 'supertest';
import axios from 'axios';

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('Home page', () => {

  beforeEach(() => {
      jest.clearAllMocks();
  });

  describe('on GET /', () => {
    test('should render home page when backend returns list of tasks', async () => {
      const tasks = [
        {
          id: 1,
          title: 'A task',
          description: 'A task description',
          Status: 'Not started',
          dueDate: '2026-10-01'
        }
      ];
      mockedAxios.get.mockResolvedValue({
        status: 200,
        data: tasks
      });


      await request(app)
        .get('/')
        .expect(res => expect(res.status).toBe(200));
      expect(mockedAxios.get).toHaveBeenCalledWith('http://localhost:4000/api/tasks')
    });

    test('should render home page (with placeholder text) when backend returns empty task array', async () => {
      mockedAxios.get.mockResolvedValue({
        status: 200,
        data: []
      });


      await request(app)
        .get('/')
        .expect(res => expect(res.status).toBe(200));
      expect(mockedAxios.get).toHaveBeenCalledWith('http://localhost:4000/api/tasks')
    });
  });
  
});
