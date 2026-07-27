import { useEffect, useState } from 'react';
import api from './api/axiosConfig';

function App() {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    api.get('/users')
      .then((response) => {
        setUsers(response.data);
      })
      .catch((error) => {
        console.error('Error fetching users:', error);
      });
  }, []);

  return (
    <div>
      <h1>Users (test)</h1>
      <pre>{JSON.stringify(users, null, 2)}</pre>
    </div>
  );
}

export default App;