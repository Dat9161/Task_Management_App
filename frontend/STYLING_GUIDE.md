# Styling Guide - Task & Project Management Tool

## Overview

This application uses a comprehensive CSS styling system with consistent design patterns, responsive layouts, loading states, error handling, and smooth animations.

## CSS Architecture

### Main Stylesheet
- **Location**: `frontend/src/style.css`
- **Import**: Imported in `main.jsx` for global availability

### Key Features

1. **Consistent Color Scheme**
   - Primary: `#646cff` (Blue)
   - Success: `#28a745` (Green)
   - Danger: `#dc3545` (Red)
   - Warning: `#ffc107` (Yellow)
   - Info: `#007bff` (Blue)

2. **Responsive Design**
   - Mobile-first approach
   - Breakpoints: 768px (tablet), 1200px (desktop)
   - Flexible grid layouts
   - Collapsible navigation

3. **Loading States**
   - Spinner animations
   - Skeleton loaders
   - Pulse effects
   - Loading text indicators

4. **Error Handling**
   - Error containers with icons
   - Alert components (success, error, warning, info)
   - Inline form validation
   - Retry functionality

5. **Animations & Transitions**
   - Fade in/out
   - Slide in (left/right)
   - Scale effects
   - Hover animations
   - Smooth transitions

## Component Styling

### Reusable Components

#### Loading Component
```jsx
import Loading from '../components/Loading';

<Loading message="Loading data..." />
```

#### ErrorMessage Component
```jsx
import ErrorMessage from '../components/ErrorMessage';

<ErrorMessage 
  title="Error Title"
  message="Error description"
  onRetry={handleRetry}
/>
```

#### Alert Component
```jsx
import Alert from '../components/Alert';

<Alert 
  type="success|error|warning|info"
  message="Alert message"
  onClose={handleClose}
  autoClose={true}
  duration={5000}
/>
```

### CSS Classes

#### Layout Classes
- `.page-container` - Main page wrapper
- `.page-header` - Page header with title and actions
- `.dashboard-container` - Dashboard specific layout
- `.auth-container` - Authentication pages layout

#### Button Classes
- `.btn-primary` - Primary action button
- `.btn-secondary` - Secondary action button
- `.btn-danger` - Destructive action button
- `.btn-success` - Success action button
- `.btn-logout` - Logout button style

#### Card Classes
- `.project-card` - Project card component
- `.task-card` - Task card component
- `.sprint-card` - Sprint card component
- `.detail-section` - Detail page sections

#### Badge Classes
- `.badge` - Base badge style
- `.status-todo`, `.status-in-progress`, `.status-done`, `.status-blocked` - Task status badges
- `.priority-low`, `.priority-medium`, `.priority-high`, `.priority-critical` - Priority badges

#### Form Classes
- `.form-group` - Form field wrapper
- `.input-error` - Error state for inputs
- `.error-text` - Error message text
- `.required` - Required field indicator

#### Animation Classes
- `.fade-in` - Fade in animation
- `.slide-in-left` - Slide from left
- `.slide-in-right` - Slide from right
- `.scale-in` - Scale up animation
- `.hover-lift` - Lift on hover
- `.hover-glow` - Glow effect on hover

#### Utility Classes
- `.text-center`, `.text-left`, `.text-right` - Text alignment
- `.mt-1` to `.mt-4` - Margin top
- `.mb-1` to `.mb-4` - Margin bottom
- `.p-1` to `.p-4` - Padding
- `.flex`, `.flex-column`, `.flex-center`, `.flex-between` - Flexbox utilities
- `.gap-1` to `.gap-4` - Gap spacing
- `.w-full`, `.h-full` - Full width/height
- `.rounded` - Border radius
- `.shadow`, `.shadow-lg` - Box shadows

## Responsive Design

### Mobile (< 768px)
- Single column layouts
- Stacked navigation
- Full-width buttons
- Collapsed filters
- Simplified grids

### Tablet (768px - 1200px)
- Two-column grids
- Responsive navigation
- Adjusted spacing
- Optimized card sizes

### Desktop (> 1200px)
- Multi-column layouts
- Full navigation
- Maximum content width: 1400px
- Optimal spacing

## Accessibility

### Features
- Focus visible states
- ARIA labels
- Keyboard navigation
- Screen reader support (`.sr-only` class)
- High contrast mode support
- Reduced motion support

### Best Practices
- Use semantic HTML
- Provide alt text for images
- Ensure sufficient color contrast
- Support keyboard-only navigation
- Test with screen readers

## Dark Mode

The application uses a dark color scheme by default:
- Background: `#242424`
- Text: `rgba(255, 255, 255, 0.87)`
- Cards: `rgba(255, 255, 255, 0.05)`
- Borders: `rgba(255, 255, 255, 0.1)`

## Performance

### Optimizations
- CSS transitions instead of JavaScript animations
- Hardware-accelerated transforms
- Efficient selectors
- Minimal repaints
- Lazy loading for images

### Best Practices
- Use CSS Grid and Flexbox
- Avoid inline styles
- Minimize CSS specificity
- Use CSS variables for theming
- Optimize animation performance

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)
- Mobile browsers (iOS Safari, Chrome Mobile)

## Customization

### Changing Colors
Update the color values in `style.css`:
```css
:root {
  --primary-color: #646cff;
  --success-color: #28a745;
  --danger-color: #dc3545;
  /* Add more custom properties */
}
```

### Adding New Animations
```css
@keyframes customAnimation {
  from {
    /* start state */
  }
  to {
    /* end state */
  }
}

.custom-class {
  animation: customAnimation 0.3s ease-out;
}
```

## Maintenance

### Adding New Styles
1. Check if existing classes can be reused
2. Follow naming conventions (BEM-like)
3. Add responsive variants if needed
4. Document new classes in this guide
5. Test across different screen sizes

### Refactoring
1. Remove unused styles regularly
2. Consolidate duplicate styles
3. Optimize selectors
4. Update documentation
5. Test thoroughly after changes

## Resources

- [CSS Grid Guide](https://css-tricks.com/snippets/css/complete-guide-grid/)
- [Flexbox Guide](https://css-tricks.com/snippets/css/a-guide-to-flexbox/)
- [Responsive Design](https://web.dev/responsive-web-design-basics/)
- [CSS Animations](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Animations)
- [Accessibility](https://www.w3.org/WAI/WCAG21/quickref/)
