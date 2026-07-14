import { ClipboardList, Package, Inbox } from 'lucide-react';

const EmptyState = ({ icon: IconComponent, message, children }) => {
  return (
    <div className="empty-state animate-fade-in">
      {IconComponent && (
        <div className="icon">
          <IconComponent size={40} strokeWidth={1.25} />
        </div>
      )}
      <p>{message}</p>
      {children && <div className="mt-4">{children}</div>}
    </div>
  );
};

export default EmptyState;
