import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axiosConfig';
import { validateRegisterForm } from '../utils/validation';

function Register() {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState({});
  const [successMessage, setSuccessMessage] = useState('');
  const navigate = useNavigate();

 const handleSubmit = async (e) => {
   e.preventDefault();
   setErrors({});
   setSuccessMessage('');

   const validationErrors = validateRegisterForm({ name, email, password });
   if (Object.keys(validationErrors).length > 0) {
     setErrors(validationErrors);
     return;
   }

   try {
     await api.post('/register', { name, email, password });
     setSuccessMessage('Registration successful! Redirecting to login...');
     setTimeout(() => navigate('/login'), 1500);
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

  return (
    <div>
      <h2>Register</h2>
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
          <label>Password</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          {errors.password && <p style={{ color: 'red' }}>{errors.password}</p>}
        </div>

        {errors.general && <p style={{ color: 'red' }}>{errors.general}</p>}
        {successMessage && <p style={{ color: 'green' }}>{successMessage}</p>}

        <button type="submit">Register</button>
      </form>
      <p>
        Already have an account? <Link to="/login">Login here</Link>
      </p>
    </div>
  );
}

export default Register;