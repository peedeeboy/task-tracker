import { app } from '../../../main/app';

import request from 'supertest';
import axios from 'axios';

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('/task route', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    });

    describe('on GET /task/add', () => {
        test('should render add new task form', async () => {
            await request(app)
                    .get('/task/add')
                    .expect(res => expect(res.status).toBe(200));
        });
    });

    describe('on POST /task/add', () => {
        test('should call persist task to backend and redirect to homepage', async () => {
            const task = {
                title: "New task",
                description: "A new task to add to database",
                status: "Not started",
                "due-date-day": "01",
                "due-date-month": "01",
                "due-date-year": "2027",
            }
            const taskToSave = {
                title: "New task",
                description: "A new task to add to database",
                status: "Not started",
                dueDate: "2027-01-01"
            }
            const persistedTask = {
                id: 10,
                title: "New task",
                description: "A new task to add to database",
                status: "Not started",
                dueDate: "2027-01-01"
            }
            mockedAxios.get.mockResolvedValue({
                status: 201,
                data: persistedTask
            });

            await request(app)
                    .post('/task/add')
                    .send(task)
                    .expect(res => expect(res.status).toBe(302));
            expect(mockedAxios.post).toHaveBeenCalledWith("http://localhost:4000/api/tasks", taskToSave)
        });
    });

    describe('on GET /task/:id', () => {
        test('should get task from backend and render edit task form', async () => {
            const persistedTask = {
                id: 10,
                title: "New task",
                description: "A new task to add to database",
                status: "Not started",
                dueDate: "2027-01-01"
            }
            mockedAxios.get.mockResolvedValue({
                status: 200,
                data: persistedTask
            });
            
            await request(app)
                    .get('/task/10')
                    .expect(res => expect(res.status).toBe(200));
             expect(mockedAxios.get).toHaveBeenCalledWith("http://localhost:4000/api/tasks/10")

        });
    });

    describe('on POST /task/:id', () => {
        test('should save task to backend and rendirect edit to home page', async () => {
            const task = {
                id: "20",
                title: "Updated task",
                description: "A task to update in database",
                status: "Complete",
                "due-date-day": "01",
                "due-date-month": "04",
                "due-date-year": "2027",
            }
            const taskToSave = {
                id: "20",
                title: "Updated task",
                description: "A task to update in database",
                status: "Complete",
                dueDate: "2027-04-01"
            }
            const persistedTask = {
                id: "20",
                title: "Updated task",
                description: "A task to update in database",
                status: "Complete",
                dueDate: "2027-04-01"
            }
            mockedAxios.get.mockResolvedValue({
                status: 200,
                data: persistedTask
            });

            await request(app)
                    .post('/task/20')
                    .send(task)
                    .expect(res => expect(res.status).toBe(302));
            expect(mockedAxios.put).toHaveBeenCalledWith("http://localhost:4000/api/tasks/20", taskToSave)
        });
    });

});