import { Application } from 'express';
import axios from 'axios';

export default function (app: Application): void {
  app.get('/task/add', async (req, res) => {
     res.render('add-task');
  });

  app.post('/task/add', async (req, res) => {
     try {
         const title = req.body['title']
         const description = req.body['description']
         const dueDateDay = req.body['due-date-day']
         const dueDateMonth = req.body['due-date-month']
         const dueDateYear = req.body['due-date-year']

         const dueDate = new Date()
         dueDate.setUTCFullYear(dueDateYear, dueDateMonth - 1, dueDateDay)

         axios.post('http://localhost:4000/api/tasks',
            {
              title: title,
              description: description,
              status: 'Not started',
              dueDate: dueDate.toISOString().split('T')[0]
            }
         );
        res.redirect('/');
     } catch (error) {
       console.error('Error making request:', error);
       res.render('error', {});
     }
  });

  app.get('/task/:id', async (req, res) => {
    try {
        const taskId = req.params.id
        const task = await axios.get(`http://localhost:4000/api/tasks/${taskId}`);
        res.render('edit-task', { "task": task.data });
    } catch (error) {
        console.error('Error making request:', error);
        res.render('error', {});
    }
  });

  app.post('/task/:id', async (req, res) => {
    try {
        const taskId = req.params.id
        const title = req.body['title']
        const description = req.body['description']
        const status = req.body['status']
        const dueDateDay = req.body['due-date-day']
        const dueDateMonth = req.body['due-date-month']
        const dueDateYear = req.body['due-date-year']

        const dueDate = new Date()
        dueDate.setUTCFullYear(dueDateYear, dueDateMonth - 1, dueDateDay)

        axios.put(`http://localhost:4000/api/tasks/${taskId}`,
            {
              id: taskId,
              title: title,
              description: description,
              status: status,
              dueDate: dueDate.toISOString().split('T')[0]
            }
        );
        res.redirect('/');
    } catch (error) {
        console.error('Error making request:', error);
        res.render('error', {});
    }
  });
}
