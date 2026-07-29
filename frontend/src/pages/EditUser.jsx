import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import api from '../api/axiosConfig';

function EditUser() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(true);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    api.get(`/users/${id}`)
      .then((response) => {
        setName(response.data.name);
        setEmail(response.data.email);
        setLoading(false);
      })
      .catch((err) => {
        setErrors({ general: 'Could not load user.' });
        setLoading(false);
      });
  }, [id]);


  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});

    try {
      await api.put(`/users/${id}`, { name, email, password });
      navigate('/users');
    } catch (err) {
      if (err.response && err.response.data) {
        const data = err.response.data;
        if (data.validationErrors) {
          setErrors(data.validationErrors);
        } else if (data.message) {
          setErrors({ general: data.message });
        }
      } else {
        setErrors({ general: 'Something went wrong. Please try again.' });
      }
    }
  };

  if (loading) return <p>Loading user...</p>;

  return (
      <div>
        <h2>Edit User</h2>
        <form onSubmit={handleSubmit}>
          <div>
            <label>Name</label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
            {errors.name && <p style={{ color: 'red' }}>{errors.name}</p>}
          </div>
          <div>
            <label>Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            {errors.email && <p style={{ color: 'red' }}>{errors.email}</p>}
          </div>
          <div>
            <label>New Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter a new password"
              required
            />
            {errors.password && <p style={{ color: 'red' }}>{errors.password}</p>}
          </div>

          {errors.general && <p style={{ color: 'red' }}>{errors.general}</p>}

          <button type="submit">Save Changes</button>
        </form>
        <Link to="/users">Cancel</Link>
      </div>
    );
  }

  export default EditUser;