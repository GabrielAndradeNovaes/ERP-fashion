import { render } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import App from './App';
import { BrowserRouter } from 'react-router-dom';

describe('App', () => {
  it('renders sem quebrar', () => {
    // We shouldn't use BrowserRouter if App already has it, let's just render App
    // I don't know if App has a router or not, but usually it does. Let's just try.
    const { container } = render(
      <App />
    );
    expect(container).toBeDefined();
  });
});
