import { Link, useNavigate} from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Dashboard(){
    const {user, logout} = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
        };

    if (!user) {
        return (
          <div>
            <p>You're not logged in.</p>
            <Link to="/login">Go to Login</Link>
          </div>
        );
      }

  return (
      <div className="dashboard-container">
        <h2>Welcome, {user.name}!</h2>
        <p>Email: {user.email}</p>
        <button onClick={handleLogout}>Logout</button>
      </div>
    );
    }

export default Dashboard;