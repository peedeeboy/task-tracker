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
}
