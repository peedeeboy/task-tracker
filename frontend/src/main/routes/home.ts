import { Application } from 'express';
import axios from 'axios';

export default function (app: Application): void {
  app.get('/', async (req, res) => {
    try {
      const tasks = await axios.get('http://localhost:4000/api/tasks');
      res.render('home', { "tasks": tasks.data });
    } catch (error) {
      console.error('Error making request:', error);
      res.render('error', {});
    }
  });
  app.get('*', function(req, res){
    res.status(404).render('not-found');
  });
}
