import { createContext, useContext, useEffect, useRef, useState } from 'react';
import { useApplicationContext } from '@/contexts/index.jsx';
import { auth } from '@/api/index.js';

const AuthenticationContext = createContext(null);
export const useAuthentication = () => useContext(AuthenticationContext);

/**
 * 전역 Authentication context
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export const AuthenticationProvider = ({ children }) => {
  const authenticationRef = useRef(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  const { api } = useApplicationContext();

  /**
   * TODO 상위 ApplicationContext에서 authentication 체크 하는 부분과 중복 호출 되는 문제 해결 필요
   */
  useEffect(() => {

    api.get(auth.authentication)
      .then(success => {
        setIsAuthenticated(success);
      });


    return () => {
      setIsAuthenticated(false);
    };
  }, []);

  return <AuthenticationContext.Provider
    isAuthenticated={isAuthenticated}
    value={{
      isAuthenticated
    }}>{children}</AuthenticationContext.Provider>;
};
