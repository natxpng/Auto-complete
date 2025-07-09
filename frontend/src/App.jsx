import { useState } from 'react';

function App() {
    const [term, setTerm] = useState('');
    const [suggestions, setSuggestions] = useState([]);

    const handleChange = async (e) => {
        const value = e.target.value;
        setTerm(value);

        const query = `
      query {
        suggestions(term: "${value}")
      }
    `;

        const response = await fetch('http://localhost:8080/graphql', {
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
        <div>
            <h1>Autocomplete</h1>
            <input
                type="text"
                value={term}
                onChange={handleChange}
                placeholder="Digite algo..."
            />
            <ul>
                {suggestions
                    .filter((item) => item.trim() !== '')
                    .map((item, index) => (
                        <li key={index}>{item}</li>
                    ))}
            </ul>
        </div>
    );
}

export default App;
