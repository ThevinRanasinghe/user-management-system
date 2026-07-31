import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axiosConfig';

function UserList() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [sortBy, setSortBy] = useState('id');
  const [direction, setDirection] = useState('asc');

  const pageSize = 5;

  const fetchUsers = async () => {
    setLoading(true);
    setError('');
    try {
      const isSearching = searchQuery.trim() !== '';
      const url = isSearching ? '/users/search' : '/users';
      const params = {
        page,
        size: pageSize,
        sortBy,
        direction,
        ...(isSearching && { query: searchQuery }),
      };

      const response = await api.get(url, { params });
      setUsers(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (err) {
      setError('Failed to load users.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [page, sortBy, direction]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchUsers();
  };

  const handleClearSearch = () => {
    setSearchQuery('');
    setPage(0);
    setTimeout(fetchUsers, 0);
  };

  const handleSort = (field) => {
    if (sortBy === field) {
      setDirection(direction === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(field);
      setDirection('asc');
    }
    setPage(0);
  };

  const handleDelete = async (id, name) => {
    const confirmed = window.confirm(`Delete user "${name}"? This cannot be undone.`);
    if (!confirmed) return;

    try {
      await api.delete(`/users/${id}`);
      setMessage(`User "${name}" deleted successfully.`);
      fetchUsers();
    } catch (err) {
      setError('Failed to delete user.');
    }
  };

  const sortIndicator = (field) => {
    if (sortBy !== field) return '';
    return direction === 'asc' ? ' ▲' : ' ▼';
  };

  if (loading) return <p>Loading users...</p>;

  return (
    <div className="page-container">
      <h2>All Users</h2>

      <form onSubmit={handleSearchSubmit} style={{ marginBottom: '16px' }}>
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
        <>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th onClick={() => handleSort('name')} style={{ cursor: 'pointer' }}>
                  Name{sortIndicator('name')}
                </th>
                <th onClick={() => handleSort('email')} style={{ cursor: 'pointer' }}>
                  Email{sortIndicator('email')}
                </th>
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

          <div style={{ marginTop: '16px', display: 'flex', gap: '12px', alignItems: 'center' }}>
            <button disabled={page === 0} onClick={() => setPage((p) => p - 1)}>
              Previous
            </button>
            <span>Page {page + 1} of {totalPages}</span>
            <button disabled={page + 1 >= totalPages} onClick={() => setPage((p) => p + 1)}>
              Next
            </button>
          </div>
        </>
      )}
    </div>
  );
}

export default UserList;