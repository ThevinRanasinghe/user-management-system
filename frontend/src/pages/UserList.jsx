import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axiosConfig';

function UserList() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await api.get('/users');
      setUsers(response.data);
    } catch (err) {
      setError('Failed to load users.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleSearch = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (searchQuery.trim() === '') {
        await fetchUsers();
      } else {
        const response = await api.get('/users/search', {
          params: { query: searchQuery },
        });
        setUsers(response.data);
      }
    } catch (err) {
      setError('Search failed.');
    } finally {
      setLoading(false);
    }
  };

  const handleClearSearch = () => {
    setSearchQuery('');
    fetchUsers();
  };

  const handleDelete = async (id, name) => {
    const confirmed = window.confirm(`Delete user "${name}"? This cannot be undone.`);
    if (!confirmed) return;

    try {
      await api.delete(`/users/${id}`);
      setMessage(`User "${name}" deleted successfully.`);
      setUsers((prevUsers) => prevUsers.filter((u) => u.id !== id));
    } catch (err) {
      setError('Failed to delete user.');
    }
  };

  if (loading) return <p>Loading users...</p>;

  return (
    <div className="page-container">
      <h2>All Users</h2>

      <form onSubmit={handleSearch} style={{ marginBottom: '16px' }}>
        <input
          type="text"
          placeholder="Search by name or email"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
        <button type="submit">Search</button>
        <button type="button" onClick={handleClearSearch}>Clear</button>
      </form>

      {message && <p className="form-success">{message}</p>}
      {error && <p className="form-error">{error}</p>}

      {users.length === 0 ? (
        <p>No users found.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{u.name}</td>
                <td>{u.email}</td>
                <td>
                  <Link to={`/users/edit/${u.id}`} className="action-link">Edit</Link>
                  <button className="delete-btn" onClick={() => handleDelete(u.id, u.name)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default UserList;