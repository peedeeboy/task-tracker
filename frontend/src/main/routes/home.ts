import { Application } from 'express';
import axios from 'axios';

export default function (app: Application): void {
  app.get('/', async (req, res) => {
    try {
      const response = await axios.get('http://localhost:4000/api/tasks');
      console.log(response.data);
      res.render('home', { "tasks": response.data });
    } catch (error) {
      console.error('Error making request:', error);
      res.render('home', {});
    }
  });
  app.get('/task/add', async (req, res) => {
     res.render('add-task');
  });
  app.post('/task/add', async (req, res) => {
     try {
         console.log(req.body)
         const title = req.body['title']
         const description = req.body['description']
         const dueDateDay = req.body['due-date-day']
         const dueDateMonth = req.body['due-date-month']
         const dueDateYear = req.body['due-date-year']

         const dueDate = `${dueDateYear}-${dueDateMonth}-${dueDateDay}`
         axios.post('http://localhost:4000/api/tasks',
            {
              title: title,
              description: description,
              status: 'Not started',
              dueDate: dueDate
            }
         );
        res.render('add-task');
     } catch (error) {
             console.error('Error making request:', error);
             res.render('home', {});
     }
  });
  app.get('*', function(req, res){
    res.status(404).render('not-found');
  });
}
