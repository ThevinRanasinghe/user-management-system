import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!user) return null;

  return (
    <nav style={{ padding: '10px', borderBottom: '1px solid #ccc', marginBottom: '20px' }}>
      <Link to="/dashboard" style={{ marginRight: '15px' }}>Dashboard</Link>
      <Link to="/users" style={{ marginRight: '15px' }}>Users</Link>
      <span style={{ marginRight: '15px' }}>Logged in as {user.name}</span>
      <button onClick={handleLogout}>Logout</button>
    </nav>
  );
}

export default Navbar;