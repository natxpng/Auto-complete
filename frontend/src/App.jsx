import { useState } from 'react';
import './App.css';

function App() {
    const [term, setTerm] = useState('');
    const [suggestions, setSuggestions] = useState([]);

    const handleChange = async (e) => {
        const value = e.target.value;
        setTerm(value);

        if (value.length < 4) {
            setSuggestions([]);
            return;
        }
        const query = `
      query {
        suggestions(term: "${value}")
      }
    `;

        const response = await fetch(import.meta.env.VITE_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ query }),
        });


        const result = await response.json();
        const data = result.data?.suggestions || [];
        setSuggestions(data);
    };

    return (
        <div className="container">
            <div className="card">
                <img src="/futebol.png" alt="Futebol" className="image" />
                <div className="content">
                    <h1>Futebusk</h1>
                    <p>Faça sua busca sobre futebol</p>
                    <div className="search-container">
                        <input
                            type="text"
                            value={term}
                            onChange={handleChange}
                            placeholder="Digite um termo..."
                        />
                        <button>BUSCAR</button>
                    </div>
                    {suggestions.length > 0 && (
                        <ul className="suggestions">
                            {suggestions
                                .filter(item => item.trim() !== '')
                                .slice(0, 20)
                                .map((item, i) => {
                                    const matchIndex = item.toLowerCase().indexOf(term.toLowerCase());
                                    if (matchIndex === -1) return null;

                                    const before = item.slice(0, matchIndex);
                                    const match = item.slice(matchIndex, matchIndex + term.length);
                                    const after = item.slice(matchIndex + term.length);

                                    return (
                                        <li
                                            key={i}
                                            onClick={() => setTerm(item)}
                                            style={{ padding: '8px', cursor: 'pointer' }}
                                        >
                                            {before}<strong>{match}</strong>{after}
                                        </li>
                                    );
                                })}

                        </ul>
                    )}
                </div>
            </div>
        </div>
    );
}

export default App;
