# Reusable UI Components

This directory contains reusable UI components for the Task & Project Management application.

## Components

### StatusBadge
Displays task or sprint status with color coding.

**Usage:**
```jsx
import StatusBadge from './components/StatusBadge';

<StatusBadge status="TODO" />
<StatusBadge status="IN_PROGRESS" />
<StatusBadge status="DONE" />
<StatusBadge status="BLOCKED" />
```

**Supported Statuses:**
- TODO (gray)
- IN_PROGRESS (blue)
- DONE (green)
- BLOCKED (red)
- PLANNED (gray)
- ACTIVE (green)
- COMPLETED (teal)

---

### PriorityBadge
Displays task priority with color coding.

**Usage:**
```jsx
import PriorityBadge from './components/PriorityBadge';

<PriorityBadge priority="LOW" />
<PriorityBadge priority="MEDIUM" />
<PriorityBadge priority="HIGH" />
<PriorityBadge priority="CRITICAL" />
```

**Supported Priorities:**
- LOW (gray)
- MEDIUM (yellow)
- HIGH (orange)
- CRITICAL (red)

---

### ProgressBar
Displays progress percentage visually with color coding based on progress level.

**Usage:**
```jsx
import ProgressBar from './components/ProgressBar';

<ProgressBar percentage={75} label="Completion" />
<ProgressBar percentage={45} showPercentage={true} />
<ProgressBar percentage={90} />
```

**Props:**
- `percentage` (number): Progress value (0-100)
- `label` (string, optional): Label text to display above the bar
- `showPercentage` (boolean, default: true): Whether to show percentage value

**Color Coding:**
- 0-29%: Red
- 30-69%: Yellow/Orange
- 70-100%: Green

---

### Sidebar
Navigation sidebar with menu items and active state highlighting.

**Usage:**
```jsx
import Sidebar from './components/Sidebar';

<Sidebar />
```

**Features:**
- Displays navigation menu with icons
- Highlights active page
- Responsive design (horizontal on mobile)
- Fixed position on desktop

---

### Modal
Reusable modal wrapper with overlay and close functionality.

**Usage:**
```jsx
import Modal from './components/Modal';
import { useState } from 'react';

function MyComponent() {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <>
      <button onClick={() => setIsOpen(true)}>Open Modal</button>
      
      <Modal 
        isOpen={isOpen} 
        onClose={() => setIsOpen(false)}
        title="My Modal Title"
      >
        <p>Modal content goes here</p>
        <button onClick={() => setIsOpen(false)}>Close</button>
      </Modal>
    </>
  );
}
```

**Props:**
- `isOpen` (boolean): Controls modal visibility
- `onClose` (function): Callback when modal should close
- `title` (string, optional): Modal title
- `children` (ReactNode): Modal content

**Features:**
- Click overlay to close
- Press Escape key to close
- Prevents body scroll when open
- Smooth animations
- Responsive design

---

### Navbar
Already implemented - displays app logo, navigation links, user info, and notification badge.

**Usage:**
```jsx
import Navbar from './components/Navbar';

<Navbar />
```

---

## Styling

All components use the shared `style.css` file for consistent styling across the application. The color scheme follows the application's design system with primary color `#646cff`.
