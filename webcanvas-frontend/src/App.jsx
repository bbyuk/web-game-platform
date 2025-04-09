import {useState} from 'react'
import './App.css'
import CanvasTest from '@pages/test/canvas';

function App() {
  const [count, setCount] = useState(0)

  return (
      <div className="App">
          <CanvasTest />
      </div>
  )
}

export default App
