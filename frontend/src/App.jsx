import { useState } from 'react';
import './App.css';

function App() {
    const [term, setTerm] = useState('');
    const [suggestions, setSuggestions] = useState([]);

    const handleChange = async (e) => {
        const value = e.target.value;
        setTerm(value);

        // Zera as sugestões se o campo tiver menos de 4 caracteres
        if (value.length < 4) {
            setSuggestions([]);
            return;
        }

        // Monta a query GraphQL
        const query = `
      query {
        suggestions(term: "${value}")
      }
    `;

        // Faz a requisição para a API
        try {
            const response = await fetch(import.meta.env.VITE_API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ query }),
            });

            if (!response.ok) {
                setSuggestions([]);
                return;
            }

            const result = await response.json();
            const data = result.data?.suggestions || [];
            setSuggestions(data);

        } catch (error) {
            console.error("Erro ao buscar sugestões:", error);
            setSuggestions([]);
        }
    };

    return (
        <div className="container">
            <div className="card">
                <div className="content">
                    <h1>Futebusk</h1>
                    <p>Faça sua busca sobre futebol</p>

                    {/* Wrapper para a busca e as sugestões, essencial para o posicionamento */}
                    <div className="search-wrapper">
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
                                    .filter(item => item && item.trim() !== '') // Adicionado verificação de null/undefined
                                    .slice(0, 20)
                                    .map((item, i) => {
                                        const matchIndex = item.toLowerCase().indexOf(term.toLowerCase());

                                        // Se não houver correspondência, mostra o item normal sem negrito
                                        if (matchIndex === -1) {
                                            return (
                                                <li key={i} onClick={() => { setTerm(item); setSuggestions([]); }}>
                                                    {item}
                                                </li>
                                            );
                                        }

                                        const before = item.slice(0, matchIndex);
                                        const match = item.slice(matchIndex, matchIndex + term.length);
                                        const after = item.slice(matchIndex + term.length);

                                        return (
                                            <li
                                                key={i}
                                                onClick={() => {
                                                    setTerm(item);
                                                    setSuggestions([]);
                                                }}
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
        </div>
    );
}

export default App;